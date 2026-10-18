// src/components/AppShell.tsx
import React from "react";

interface AppShellProps {
  children: React.ReactNode;
}

export function AppShell({ children }: AppShellProps) {
  return (
    <div className="min-h-screen w-full bg-bg text-ink flex justify-center">
      <div className="w-full max-w-[480px] min-h-screen flex flex-col px-5 py-6">
        {children}
      </div>
    </div>
  );
}
