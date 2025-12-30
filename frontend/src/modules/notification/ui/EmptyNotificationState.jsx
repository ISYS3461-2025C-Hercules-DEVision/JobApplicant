function EmptyNotificationState() {
  return (
    <div
      className="
        bg-white border-4 border-black p-10 text-center
        shadow-[6px_6px_0px_0px_rgba(0,0,0,1)]
      "
    >
      <h2 className="text-2xl font-black uppercase mb-4">
        No notifications yet
      </h2>

      <p className="font-bold mb-6">
        When new jobs match your search profile, you’ll see them here instantly.
      </p>

      <div className="inline-block px-6 py-3 border-4 border-black font-black">
        Waiting for matches…
      </div>
    </div>
  );
}

export default EmptyNotificationState;
