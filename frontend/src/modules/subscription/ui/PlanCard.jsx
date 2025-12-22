function PlanCard({
  title,
  price,
  highlight = false,
  features = [],
  footer,
}) {
  return (
    <div
      className={`
        bg-white border-4 border-black p-8
        shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
      `}
    >
      <h3
        className={`text-2xl font-black uppercase mb-2 ${
          highlight ? "text-primary" : ""
        }`}
      >
        {title}
      </h3>

      <div className={`text-4xl font-black mb-6 ${highlight ? "text-primary" : ""}`}>
        {price}
        <span className="text-base font-bold text-black"> / month</span>
      </div>

      <ul className="font-bold space-y-3">
        {features.map((f) => (
          <li key={f}>✓ {f}</li>
        ))}
      </ul>

      {footer && <div className="mt-6">{footer}</div>}
    </div>
  );
}

export default PlanCard;
