(ns sketchpad.app-cmd
  (:require [clojure.string :as s]))

(defn take-str
  ([s n]
   (take-str 0 n))
  ([s start end]
   (str (s/join (take (- end start) (str (s/join (drop start s))))))))

(defn handle-open-cmd
  [cmd]
  (println cmd))

(def app-commands {"o:" handle-open-cmd "nf:" #()})

(defn cmd-prefix?
  [s]
  (let [tokens (s/split s #":")
        cmd-token (first tokens)
        cmd (if (= (take-str cmd-token 0 1) "(")
              (take-str s 1 (- (count cmd-token) 1))
              cmd-token)
        type (atom :repl-cmd)]
    ; (println (take-str s 1 3) (take-str s 0 2))
    (contains? (keys app-commands) (take-str s 0 2))
    (cond
      (contains? (keys app-commands) cmd)
      (swap! type (fn [_] :app-cmd))
      :else
      (swap! type (fn [_] :repl-cmd)))
    ; (println @type)
    @type))


(defn handle-app-cmd
  [cmd]
  (println cmd))
