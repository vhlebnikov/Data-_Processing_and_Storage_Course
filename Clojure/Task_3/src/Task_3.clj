(ns Task_3)

(defn prepare-blocks [coll]
  (let [cores-number (.availableProcessors (Runtime/getRuntime))
        block-size (quot (count coll) cores-number)]
    (partition-all block-size coll)))

(defn future-task [pred block]
  (->> block
       (filter pred)
       (doall)
       (future)
       ))

(defn parallel-filter
  {:test #(do
            (assert (=
                      (doall (parallel-filter odd? (range 10000)))
                      (doall (filter odd? (range 10000)))))
            )}
  ([pred coll]
   (let [blocks (prepare-blocks coll)]
     (lazy-seq
       (->> blocks
            (map (fn [block] (future-task pred block)))
            (doall)
            (map deref)
            (reduce concat)
            )
       ))))

(test #'parallel-filter)

(defmacro bench-ones [& body]
  `(with-out-str
     (time
       ~@body)))

; не работает должным образом
(defn bench [func n]
  (repeatedly (dec n) func)
  (bench-ones func))

(defn heavy-pred [args]
  (Thread/sleep 10)
  (even? args))

(print "Parallel filter" (bench-ones (doall (parallel-filter heavy-pred (range 1000)))))
(print "Build-in filter" (bench-ones (doall (filter heavy-pred (range 1000)))))
;Parallel filter "Elapsed time: 970.3947 msecs"
;Build-in filter "Elapsed time: 15602.1289 msecs"