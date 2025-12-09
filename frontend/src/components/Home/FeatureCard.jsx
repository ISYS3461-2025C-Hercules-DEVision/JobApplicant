function FeatureCard({ icon, title, description }) {
  return (
    <div className="border-4 border-black bg-white p-8 hover:translate-x-2 hover:translate-y-2 hover:shadow-none shadow-[8px_8px_0px_0px_rgba(0,0,0,1)] transition-none">
      <div className="w-20 h-20 mb-6 flex items-center justify-center border-4 border-black bg-light-gray">
        {icon}
      </div>
      <h3 className="text-2xl font-black text-black mb-4 uppercase">{title}</h3>
      <p className="text-black font-bold leading-relaxed">{description}</p>
    </div>
  );
}

export default FeatureCard;
