function JobTitleInput({ value, onChange }) {
  return (
    <div className="bg-white border-4 border-black p-6 mb-10 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
      <h2 className="text-2xl font-black uppercase mb-4">
        Desired Job Titles
      </h2>

      <input
        value={value}
        onChange={(e) => onChange(e.target.value)}
        placeholder="Software Engineer; Backend Developer;"
        className="w-full border-4 border-black px-3 py-2 font-bold"
      />
    </div>
  );
}

export default JobTitleInput;
