function SalaryRangeInput({ value, onChange }) {
  return (
    <div className="bg-white border-4 border-black p-6 mb-8 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
      <h2 className="text-2xl font-black uppercase mb-4">
        Salary Range (USD)
      </h2>

      <div className="flex gap-4">
        <input
          type="number"
          placeholder="Minimum"
          value={value.min}
          onChange={(e) => onChange({ ...value, min: e.target.value })}
          className="w-full border-4 border-black px-3 py-2 font-bold"
        />
        <input
          type="number"
          placeholder="Maximum"
          value={value.max}
          onChange={(e) => onChange({ ...value, max: e.target.value })}
          className="w-full border-4 border-black px-3 py-2 font-bold"
        />
      </div>

      <p className="mt-4 text-sm font-bold">
        Jobs without declared salary will still be included.
      </p>
    </div>
  );
}

export default SalaryRangeInput;
