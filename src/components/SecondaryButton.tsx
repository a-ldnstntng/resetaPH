// src/components/SecondaryButton.tsx
import React from "react";
import clsx from "clsx";

interface SecondaryButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
  icon?: React.ReactNode;
  variant?: "outline" | "text";
}

export function SecondaryButton({
  children,
  icon,
  variant = "outline",
  className,
  disabled,
  type = "button",
  ...rest
}: SecondaryButtonProps) {
  return (
    <button
      type={type}
      disabled={disabled}
      className={clsx(
        "w-full min-h-[56px] px-5 py-3 rounded-[16px] text-body-strong flex items-center justify-center gap-2 transition-colors duration-150 ease-out cursor-pointer select-none",
        variant === "outline"
          ? "bg-surface border border-border text-ink hover:bg-bg"
          : "bg-transparent text-ink-muted hover:text-ink",
        disabled && "cursor-not-allowed bg-bg text-ink-muted hover:bg-bg",
        className
      )}
      {...rest}
    >
      {icon && <span aria-hidden="true" className="shrink-0 flex items-center">{icon}</span>}
      <span>{children}</span>
    </button>
  );
}
