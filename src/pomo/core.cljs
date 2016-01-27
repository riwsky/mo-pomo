(ns pomo.core
  (:require [pomo.components :refer [pomo-ui]]
            [pomo.audio :refer [play-sound]]
            [pomo.notifications :refer [notify]]))

(def DEFAULT_SECONDS (* 60 25))

(enable-console-print!)

(defonce app-state (atom {:state :stop :seconds DEFAULT_SECONDS}))

(defn render! []
  (.render js/React
           (pomo-ui app-state)
           (.getElementById js/document "app")))

(defn decrease-if-play [{:keys [state seconds] :as d}]
  (if (#{:play :break} state)
    (update-in d [:seconds] dec)
    d)
  )

(defn tick-down [data]
  (.setInterval js/window #(swap! data decrease-if-play)
                1000))

(defonce tick-interval-id (tick-down app-state))

(add-watch app-state :times-up (fn [_ _ {olds :seconds} {news :seconds st :state}]
                                 (when (and (= olds 0) (= news -1))
                                   (play-sound)
                                   (let [copy {:play ["pomodoro complete!" :break (* 60 5)]
                                               :break ["break complete!" :stop DEFAULT_SECONDS]}
                                         [notification-title next-state next-seconds] (st copy)
                                         ]
                                     (notify notification-title)
                                     (reset! app-state {:state next-state :seconds next-seconds})
                                     )
                                   )))

(add-watch app-state :on-change (fn [_ _ _ _ ] (render!)))

(add-watch app-state :log-status-changes (fn [_ _ {os :state} {s :state secs :seconds}]
                                           (when (not= os s)
                                             (print (str (.Date js/window) ": " (name s))))))

(add-watch app-state :stops (fn [_ _ {os :state} {s :state}]
                              (when (and (= s :stop) (not= os :stop))
                                (swap! app-state assoc-in [:seconds] DEFAULT_SECONDS))))



(render!)
