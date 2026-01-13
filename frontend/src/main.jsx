import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './app/App.jsx'
import {Provider} from "react-redux";
import {store} from "./StateManagement/Store/store.js";
console.log("MODE =", import.meta.env.MODE);
console.log("VITE_API_BASE =", import.meta.env.VITE_API_BASE);
console.log("VITE_API_BASE_JOB_MANAGER =", import.meta.env.VITE_API_BASE_JOB_MANAGER);
createRoot(document.getElementById('root')).render(
  <StrictMode>
      <Provider store={store}>
          <App />

      </Provider>
  </StrictMode>,
)
