interface SwitchLogoProps {
  className?: string;
  size?: "sm" | "md" | "lg";
  assetBase?: string;
}

type BootstrapWindow = Window & {
  HR_PORTAL_BOOTSTRAP?: {
    resourceBase?: string;
  };
};

export function SwitchLogo({ className = "", size = "md", assetBase = "" }: SwitchLogoProps) {
  let resolvedBase = assetBase;
  if (!resolvedBase && typeof window !== "undefined") {
    const bootstrapWindow = window as BootstrapWindow;
    resolvedBase = bootstrapWindow.HR_PORTAL_BOOTSTRAP?.resourceBase || "";
  }

  const sizeClasses: Record<string, string> = {
    sm: "w-20 h-12",
    md: "w-32 h-20",
    lg: "w-48 h-30",
  };

  const normalizedBase = resolvedBase.endsWith("/") ? resolvedBase.slice(0, -1) : resolvedBase;
  const src = normalizedBase ? `${normalizedBase}/switch-logo.png` : "/switch-logo.png";

  const classes = [sizeClasses[size] ?? sizeClasses.md, "object-contain", className]
    .filter(Boolean)
    .join(" ");

  return <img src={src} alt="Switch Logo" className={classes} loading="lazy" />;
}
