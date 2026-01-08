import { useState } from "react";

function SkillTagInput({ skills, setSkills }) {
  const [input, setInput] = useState("");

  const addSkill = () => {
    const trimmed = input.trim();
    if (trimmed && !skills.includes(trimmed)) {
      setSkills([...skills, trimmed]);
      setInput("");
    }
  };

  const removeSkill = (skillToRemove) => {
    setSkills(skills.filter((s) => s !== skillToRemove));
  };

  return (
    <div className="bg-white border-4 border-black p-6 mb-8 shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]">
      <h2 className="text-2xl font-black uppercase mb-4">
        Technical Background
      </h2>

      <div className="flex gap-3 mb-4">
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="e.g. Kafka"
          className="flex-grow border-4 border-black px-3 py-2 font-bold"
        />
        <button
          onClick={addSkill}
          className="border-4 border-black px-6 font-black hover:bg-black hover:text-white"
        >
          Add
        </button>
      </div>

      <div className="flex flex-wrap gap-4">
        {skills.map((skill) => (
          <div key={skill} className="flex items-center gap-2">
            {/* Tag */}
            <span className="px-4 py-1 border-2 border-black font-bold">
              {skill}
            </span>

            {/* X button (outside tag) */}
            <button
              onClick={() => removeSkill(skill)}
              className="text-black font-black hover:text-red-600 leading-none"
              aria-label={`Remove ${skill}`}
            >
              x
            </button>
          </div>
        ))}
      </div>
    </div>
  );
}

export default SkillTagInput;
