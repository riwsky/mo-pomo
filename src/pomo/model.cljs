(ns pomo.model)

(defn play [data]
  (assoc data :state :play :since (:current data)))

(defn break [data]
  (assoc data :state :break :since (:current data)))

(defn stop [data]
  (assoc data :state :stop :already 0))

(def TOTAL_SECONDS {:play (* 60 25) :break (* 60 5)})

(defn seconds-between [d1 d2]
  (let [millis-passed (- (.getTime d1) (.getTime d2))
        secs-passed (/ millis-passed 1000)]
    secs-passed))

(defn seconds-passed [data]
  (case (:state data)
    :stop 0
    :pause (:already data)
    (+ (:already data) (seconds-between (:current data) (:since data)))))

(defn pause [data]
  (assoc data :state :pause :since (:current data) :already (seconds-passed data)))


(defn seconds-remaining [{s :state :as data}]
  (let [total-seconds (or (s TOTAL_SECONDS) (* 60 25) s)
        passed-seconds (seconds-passed data)]
      (- total-seconds passed-seconds)))

(defn now []
  (let [date (.-Date js/window)]
    (date.)))

(def fns
  {:play play
   :pause pause
   :stop stop
   :break break})
