import styles from "./Card.module.scss";

interface CardProps {
  title?: string;
  children: React.ReactNode;
  className?: string;
  bodyClassName?: string;
}

export default function Card({
  title,
  children,
  className = "",
  bodyClassName = "",
}: CardProps) {
  return (
    <div className={`${styles.card} ${className}`}>
      {title && (
        <div className={styles.header}>
          <h3 className={styles.title}>{title}</h3>
        </div>
      )}
      <div className={`${styles.body} ${bodyClassName}`}>{children}</div>
    </div>
  );
}
