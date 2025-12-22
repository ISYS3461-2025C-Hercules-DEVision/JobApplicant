import { useEffect, useState } from "react";
import NotificationItem from "./NotificationItem";
import EmptyNotificationState from "./EmptyNotificationState";

function NotificationPanel() {
  const [notifications, setNotifications] = useState([]);

  // Simulate Kafka real-time events
  useEffect(() => {
    const mockKafkaStream = setInterval(() => {
      const newNotification = {
        id: Date.now(),
        jobTitle: "Backend Engineer",
        company: "TechNova Solutions",
        location: "Ho Chi Minh City",
        matchedSkills: ["Node.js", "MongoDB"],
        time: new Date().toLocaleTimeString(),
      };

      setNotifications((prev) => [newNotification, ...prev]);
    }, 8000);

    return () => clearInterval(mockKafkaStream);
  }, []);

  if (notifications.length === 0) {
    return <EmptyNotificationState />;
  }

  return (
    <div className="space-y-6">
      {notifications.map((item) => (
        <NotificationItem key={item.id} data={item} />
      ))}
    </div>
  );
}

export default NotificationPanel;
