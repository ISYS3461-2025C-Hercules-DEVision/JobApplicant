import { useState } from "react";
import SectionWrapper from "../../../components/SectionWrapper/SectionWrapper";

function ActivitySection() {
  const [activities] = useState([1, 2, 3]);

  return (
    <SectionWrapper
      title="Activity"
      onEdit={() => console.log("edit activity")}
      onAdd={() => console.log("add new activity")}
    >
      <div className="grid md:grid-cols-3 gap-4">
        {activities.map((i) => (
          <div
            key={i}
            className="
              border-4 border-black bg-white p-3
              shadow-[4px_4px_0px_0px_rgba(0,0,0,1)]
              hover:translate-x-1 hover:translate-y-1 hover:shadow-none
              transition-none
            "
          >
            <img
              src={`https://picsum.photos/400?random=${i}`}
              className="w-full h-32 object-cover border-2 border-black"
            />

            <p className="font-black mt-2">Wrapping up our Capstone Journey!</p>
            <p className="text-sm text-gray-600">3w ago</p>

            <div className="flex items-center gap-4 mt-2 text-sm">
              <span>❤️ 1,498</span>
              <span>💬 3,000</span>
            </div>
          </div>
        ))}
      </div>
    </SectionWrapper>
  );
}

export default ActivitySection;
