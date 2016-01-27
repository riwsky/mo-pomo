(ns pomo.notifications)

(defonce notifications (atom (= "granted" (.-permission js/Notification))))

(when (= "default" (.-permission js/Notification))
  "default" (.requestPermission js/Notification #(reset! notifications (= % "granted"))))

(defn notify
  ([title]
   (notify title nil))
  ([title body]
   (when @notifications
     (js/Notification. title (when body #js {"body" body})))))
