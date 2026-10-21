package ph.reseta.app.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import ph.reseta.model.PrescriptionItem
import java.util.Locale

/**
 * Text-to-Speech manager for dosage schedule playback (Rule 4).
 *
 * Rules:
 * - Checks for installed fil-PH voice at runtime.
 * - If none exists, falls back to system voice and displays a clear notice.
 * - Never claims it works offline; notes that internet may be required.
 * - Builds spoken text strictly from schedule fields (generic, strength, form, dosesPerDay, slots, mealNote, durationDays).
 */
class ResetaTtsManager(private val context: Context) : TextToSpeech.OnInitListener {

    sealed class TtsState {
        object Uninitialized : TtsState()
        data class Ready(val hasFilipinoVoice: Boolean, val notice: String?) : TtsState()
        object Speaking : TtsState()
        data class Error(val message: String) : TtsState()
    }

    private var tts: TextToSpeech? = null
    private var hasFilVoice: Boolean = false
    private var voiceNotice: String? = null

    private val _state = MutableStateFlow<TtsState>(TtsState.Uninitialized)
    val state: StateFlow<TtsState> = _state.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            setupVoice()
        } else {
            _state.value = TtsState.Error("Hindi masimulan ang Text-to-Speech sa device na ito.")
        }
    }

    private fun setupVoice() {
        val t = tts ?: return

        val filLocale = Locale.forLanguageTag("fil-PH")
        val tlLocale = Locale.forLanguageTag("tl-PH")

        val filAvailable = t.isLanguageAvailable(filLocale)
        val tlAvailable = t.isLanguageAvailable(tlLocale)

        val voiceList = runCatching { t.voices }.getOrNull()
        val filVoice = voiceList?.firstOrNull { voice ->
            val loc = voice.locale
            loc.language.equals("fil", ignoreCase = true) ||
            (loc.language.equals("tl", ignoreCase = true) && loc.country.equals("PH", ignoreCase = true))
        }

        val discoveredFilVoice = (filAvailable >= TextToSpeech.LANG_AVAILABLE) ||
                                (tlAvailable >= TextToSpeech.LANG_AVAILABLE) ||
                                (filVoice != null)

        val notice: String?
        if (discoveredFilVoice) {
            if (filVoice != null) {
                runCatching { t.voice = filVoice }
            } else if (filAvailable >= TextToSpeech.LANG_AVAILABLE) {
                t.language = filLocale
            } else {
                t.language = tlLocale
            }
            notice = null
        } else {
            // Fallback to default locale. Strictly comply with Rule 4: Do not claim it works offline.
            t.language = Locale.getDefault()
            notice = "Walang nakitang Filipino (fil-PH) voice sa device na ito. Gagamitin ang default voice ng device. Paalala: Maaaring kailangan ng internet connection para sa boses at hindi ito gumagana offline."
        }

        this.hasFilVoice = discoveredFilVoice
        this.voiceNotice = notice

        t.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _state.value = TtsState.Speaking
            }

            override fun onDone(utteranceId: String?) {
                _state.value = TtsState.Ready(hasFilVoice, voiceNotice)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _state.value = TtsState.Ready(hasFilVoice, voiceNotice)
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _state.value = TtsState.Ready(hasFilVoice, voiceNotice)
            }
        })

        _state.value = TtsState.Ready(hasFilVoice, voiceNotice)
    }

    /**
     * Requirement 3: Build spoken text ONLY from schedule fields:
     * genericName, strength, form, dosesPerDay, slots, mealNote, durationDays.
     */
    fun buildScheduleSpeechText(items: List<PrescriptionItem>, isEnglish: Boolean = false): String {
        if (items.isEmpty()) {
            return if (isEnglish) "No scheduled medications found." else "Walang nakitang gamot sa iskedyul."
        }

        val sb = StringBuilder()
        if (isEnglish) {
            sb.append("Medication schedule. ")
            items.forEachIndexed { index, item ->
                sb.append("Medication ${index + 1}: ${item.genericName} ${item.strength} ${item.form}. ")
                if (item.dosesPerDay > 0) {
                    sb.append("Take ${item.dosesPerDay} time")
                    if (item.dosesPerDay > 1) sb.append("s")
                    sb.append(" a day")
                    if (item.slots.isNotEmpty()) {
                        sb.append(" during ${item.slots.joinToString(", ")}")
                    }
                    sb.append(". ")
                }
                if (!item.mealNoteEn.isNullOrBlank()) {
                    sb.append("${item.mealNoteEn}. ")
                }
                val durationEn = item.durationDays
                if (durationEn != null && durationEn > 0) {
                    sb.append("For $durationEn days. ")
                }
            }
        } else {
            sb.append("Iskedyul ng pag-inom ng gamot. ")
            items.forEachIndexed { index, item ->
                sb.append("Gamot ${index + 1}: ${item.genericName} ${item.strength} ${item.form}. ")
                if (item.dosesPerDay > 0) {
                    sb.append("Uminom ng ${item.dosesPerDay} beses sa isang araw")
                    if (item.slots.isNotEmpty()) {
                        sb.append(" tuwing ${item.slots.joinToString(", ")}")
                    }
                    sb.append(". ")
                }
                if (!item.mealNoteTl.isNullOrBlank()) {
                    sb.append("${item.mealNoteTl}. ")
                }
                val durationTl = item.durationDays
                if (durationTl != null && durationTl > 0) {
                    sb.append("Inumin sa loob ng $durationTl araw. ")
                }
            }
        }

        return sb.toString().trim()
    }

    fun speakSchedule(items: List<PrescriptionItem>, isEnglish: Boolean = false) {
        val t = tts ?: return
        val text = buildScheduleSpeechText(items, isEnglish)
        t.speak(text, TextToSpeech.QUEUE_FLUSH, null, "schedule_utterance")
    }

    fun stop() {
        tts?.stop()
        val cur = _state.value
        if (cur is TtsState.Speaking) {
            _state.value = TtsState.Ready(
                hasFilipinoVoice = hasFilVoice,
                notice = voiceNotice
            )
        }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
