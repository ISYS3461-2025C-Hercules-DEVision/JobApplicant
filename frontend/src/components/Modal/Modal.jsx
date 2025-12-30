function Modal({ title, children, onClose }) {
  return (
    <div
      className="
        fixed inset-0 z-50 flex items-center justify-center
        bg-black bg-opacity-50
      "
      onClick={onClose}
    >
      {/* Modal Box */}
      <div
        className="
          bg-white border-4 border-black
          w-full max-w-lg
          shadow-[8px_8px_0px_0px_rgba(0,0,0,1)]
        "
        onClick={(e) => e.stopPropagation()}
      >
        {/* Header */}
        <div className="flex items-center justify-between px-6 py-4 border-b-4 border-black">
          <h2 className="text-xl font-black uppercase">
            {title}
          </h2>

          <button
            onClick={onClose}
            className="
              w-10 h-10 border-2 border-black
              flex items-center justify-center
              font-black text-lg
              hover:bg-black hover:text-white
              transition-none
            "
          >
            ✕
          </button>
        </div>

        {/* Content */}
        <div className="p-6">
          {children}
        </div>
      </div>
    </div>
  );
}

export default Modal;
