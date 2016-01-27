(ns pomo.components
  (:require [sablono.core :as sab]
            [clojure.string :as string]
            [pomo.audio :as audio]
            [goog.string :as gstring]
            [goog.string.format]
            )
  )

(def state-colors {:stop "red" :play "primary" :pause ""})

(defn button [state on-click]
  (let [color (state state-colors)
        text (string/capitalize (name state))
        icon-class (name state)]
    [:button.ui.labeled.icon.button {:key state :class color :onClick on-click}
     [:i.icon {:key "icon" :class icon-class}] text]))

(def visible-states
  {:play [:pause :stop]
   :pause [:play :stop]
   :stop [:play]
   :break [:stop]})

(defn fmtsecs [secs]
  (gstring/format "%02d:%02d" (quot secs 60) (rem secs 60)))

(defn pomo-ui [data]
  (sab/html [:div.ui.container
             [:h1 (fmtsecs (:seconds @data))]
             [:div#buttons
              (let [state (:state @data)
                    next-states (state visible-states)
                    on-click (fn [to] #(swap! data assoc-in [:state] to))
                    buttons (map #(button % (on-click %)) next-states)
                    ]
                buttons
                )
              ]]))
