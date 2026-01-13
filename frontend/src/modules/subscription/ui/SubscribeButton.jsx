function SubscribeButton({ onClick, label = "SUBSCRIBE NOW" }) {
  return (
    <div className="flex justify-center mt-10">
      <button
        onClick={onClick}
        className="
          w-full max-w-md bg-primary text-white font-black uppercase
          border-4 border-black py-4
          hover:translate-x-1 hover:translate-y-1 hover:shadow-none
          shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
          transition-none
        "
      >
        {label}
      </button>
    </div>
  );
}

export default SubscribeButton;
