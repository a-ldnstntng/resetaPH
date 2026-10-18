// src/components/PrimaryButton.tsx
import React from "react";
import clsx from "clsx";

interface PrimaryButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  children: React.ReactNode;
  icon?: React.ReactNode;
}

export function PrimaryButton({
  children,
  icon,
  className,
  disabled,
  type = "button",
  ...rest
}: PrimaryButtonProps) {
  return (
    <button
      type={type}
      disabled={disabled}
      className={clsx(
        "w-full min-h-[56px] px-5 py-3 rounded-[16px] bg-primary text-surface text-body-strong flex items-center justify-center gap-2 transition-opacity duration-150 ease-out cursor-pointer select-none",
        disabled ? "opacity-50 cursor-not-allowed" : "hover:opacity-95 active:opacity-90",
        className
      )}
      {...rest}
    >
      {icon && <span aria-hidden="true" className="shrink-0 flex items-center">{icon}</span>}
      <span>{children}</span>
    </button>
  );
}
