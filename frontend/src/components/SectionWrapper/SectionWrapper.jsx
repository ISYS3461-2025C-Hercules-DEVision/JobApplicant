function SectionWrapper({ 
  title, 
  children, 
  onEdit, 
  onAdd, 
  className = "" 
}) {
  return (
    <div 
      className={`
        relative bg-white border-4 border-black p-6 
        shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
        ${className}
      `}
    >

      {/* Edit button (top-right) */}
      {onEdit && (
        <button
          onClick={onEdit}
          className="absolute top-7 right-7 text-black hover:text-primary"
        >
          <svg 
            className="w-5 h-5" 
            fill="none" 
            stroke="currentColor" 
            strokeWidth="3" 
            viewBox="0 0 24 24"
          >
            <path d="M12 20h9" />
            <path d="M16.5 3.5a2.121 2.121 0 013 3L7 19l-4 1 1-4L16.5 3.5z" />
          </svg>
        </button>
      )}

      {/* Title */}
      {title && (
        <h3 className="text-xl font-black uppercase mb-4">{title}</h3>
      )}

      {/* Section Content */}
      {children}

      {/* Add Button (bottom-center) */}
      {onAdd && (
        <div className="flex justify-center mt-6">
          <button
            onClick={onAdd}
            className="
                w-full py-3 
                border-4 border-black 
                bg-white text-black font-black text-xl
                hover:bg-primary hover:text-white
                hover:translate-x-1 hover:translate-y-1 hover:shadow-none
                shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]
                transition-none
            "
          >
            +
          </button>
        </div>
      )}

    </div>
  );
}

export default SectionWrapper;
