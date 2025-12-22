function EmploymentTypeSelector({ selected, setSelected }) {
  const options = [
    "Full-time",
    "Part-time",
    "Fresher",
    "Internship",
    "Contract",
  ];

  const toggle = (type) => {
    setSelected((prev) =>
      prev.includes(type)
        ? prev.filter((t) => t !== type)
        : [...prev, type]
    );
  };

  return (
    <div className="bg-white border-4 border-black p-6 mb-8 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
      <h2 className="text-2xl font-black uppercase mb-4">
        Employment Type
      </h2>

      <div className="grid grid-cols-2 gap-4">
        {options.map((type) => (
          <label key={type} className="flex items-center gap-3 font-bold">
            <input
              type="checkbox"
              checked={selected.includes(type)}
              onChange={() => toggle(type)}
              className="w-5 h-5"
            />
            {type}
          </label>
        ))}
      </div>

      <p className="mt-4 text-sm font-bold">
        If Full-time and Part-time are not selected, both will be included.
      </p>
    </div>
  );
}

export default EmploymentTypeSelector;
