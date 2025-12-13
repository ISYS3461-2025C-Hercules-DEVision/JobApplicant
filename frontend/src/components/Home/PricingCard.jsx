function PricingCard({ title, price, features, highlighted }) {
  return (
    <div className={`${highlighted ? 'bg-primary text-white' : 'bg-white text-black'} border-4 border-black p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none`}>
      
      <div className="border-b-4 border-black pb-6 mb-6">
        <h3 className="text-2xl font-black mb-2 uppercase">{title}</h3>
        <div className="text-5xl font-black">{price}</div>
      </div>

      <ul className="space-y-4">
        {features.map((f, idx) => (
          <li key={idx} className="flex items-start">
            <span className="font-black text-2xl mr-3">{highlighted ? '✓' : '✓'}</span>
            <span className="font-bold">{f}</span>
          </li>
        ))}
      </ul>

    </div>
  );
}

export default PricingCard;
