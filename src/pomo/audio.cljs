(ns pomo.audio)

(defn play-sound []
  (let [audio (.querySelector js/document "audio") ]
    (.play audio)
    )
  )
