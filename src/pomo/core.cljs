(ns pomo.core
  (:require [pomo.components :refer [pomo-ui fmtsecs]]
            [pomo.audio :refer [play-sound]]
            [pomo.model :as model]
            [pomo.notifications :refer [notify]]))

(enable-console-print!)

(defonce app-state (atom {:state :stop :since (model/now) :current (model/now)}))

(defn render! []
  (.render js/React
           (pomo-ui app-state)
           (.getElementById js/document "app")))


(defn track-current-time [data]
  (.setInterval js/window #(swap! data assoc :current (model/now))
                500))

(defonce tick-interval-id (track-current-time app-state))

(defn update-title [to]
  (aset js/document "title" to))

(add-watch app-state :update-title (fn [_ _ _ n]
                                     (update-title (fmtsecs (model/seconds-remaining n)))))

(add-watch app-state :times-up (fn [_ _ o n]
                                 (when (and (>= (model/seconds-remaining o) 0) (< (model/seconds-remaining n) 0))
                                   (play-sound)
                                   (let [copy {:play ["pomodoro complete!" :break]
                                               :break ["break complete!" :stop]}
                                         [notification-title next-state] ((:state n) copy)
                                         ]
                                     (notify notification-title)
                                     (swap! app-state assoc :state next-state)
                                     )
                                   )))

(add-watch app-state :on-change (fn [_ _ _ _ ] (render!)))

(add-watch app-state :log-status-changes (fn [_ _ {os :state} {s :state secs :seconds}]
                                           (when (not= os s)
                                             (print (str (.Date js/window) ": " (name s))))))


(render!)
