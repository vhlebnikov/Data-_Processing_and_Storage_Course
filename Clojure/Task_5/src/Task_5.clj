(ns Task_5)

(def philosophers-number 5)
(def thinking-time (long (rand-int 500)))
(def dining-time (long (rand-int 500)))
(def periods-number 1)

(def forks (take philosophers-number (repeatedly #(ref 0 :validator (fn [x] (<= x 1))))))
(def transaction-restarts (atom 0 :validator #(>= % 0)))

(defn philosopher [id left-fork right-fork]
  (dotimes [period periods-number]
    (do
      (println (str "Period: " period " [Philosopher " id " starts thinking]"))
      (Thread/sleep thinking-time)
      (dosync
        (swap! transaction-restarts inc)
        (alter left-fork inc)
        (println (str "Period: " period " [Philosopher " id " picked up left fork]"))
        (alter right-fork inc)
        (println (str "Period: " period " [Philosopher " id " picked up right fork]"))
        (Thread/sleep dining-time)
        (println (str "Period: " period " [Philosopher " id " finished dining]"))
        (alter right-fork dec)
        (println (str "Period: " period " [Philosopher " id " put right fork back]"))
        (alter left-fork dec)
        (println (str "Period: " period " [Philosopher " id " put left fork back]"))
        (swap! transaction-restarts dec)))))

(def philosophers
  (map #(new Thread
             (fn []
               (philosopher %
                            (nth forks %)
                            (nth forks (mod (inc %) philosophers-number)))))
       (range philosophers-number)))

(defn start-philosophers []
  (do
    (doall (map #(.start %) philosophers))
    (doall (map #(.join %) philosophers))))

(time (start-philosophers))
(println (str "Transactions restarted: " (deref transaction-restarts)))