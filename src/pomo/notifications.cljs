(ns pomo.notifications)

(defn get-permission []
  (or (if-let [Notification (.-Notification js/window)]
        (.-permission Notification)))
  "denied")

(defonce notifications
  (atom (= "granted" (get-permission)))

(when (= "default" (get-permission))
  "default" (.requestPermission js/Notification #(reset! notifications (= % "granted"))))

(defn notify
  ([title]
   (notify title nil))
  ([title body]
   (when @notifications
     (js/Notification. title (when body #js {"body" body})))))
