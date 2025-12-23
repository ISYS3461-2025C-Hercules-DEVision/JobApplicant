// import { useEffect, useState } from "react";
// import { subscriptionService } from "../services/subscriptionService";

// export function useSubscription() {
//   const [subscription, setSubscription] = useState(null);
//   const [loading, setLoading] = useState(true);

//   useEffect(() => {
//     subscriptionService
//       .getMySubscription()
//       .then(res => setSubscription(res.data))
//       .finally(() => setLoading(false));
//   }, []);

//   return {
//     subscription,
//     isPremium: subscription?.isActive === true,
//     loading,
//   };
// }
