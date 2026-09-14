import styles from "./Button.module.scss";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "secondary" | "danger" | "ghost";
  size?: "sm" | "md" | "icon";
  children: React.ReactNode;
}

export default function Button({
  variant = "primary",
  size = "md",
  children,
  className = "",
  ...props
}: ButtonProps) {
  const sizeClass = size === "sm" ? styles.sm : size === "icon" ? styles.icon : "";
  return (
    <button
      className={`${styles.button} ${styles[variant]} ${sizeClass} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}
