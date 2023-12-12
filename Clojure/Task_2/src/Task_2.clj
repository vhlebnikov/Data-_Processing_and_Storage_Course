(ns Task_2)

(defmacro bench [& body]
  `(with-out-str
     (time
       ~@body)))

(defn divides? [x y] (zero? (rem x y)))

(def primes
  (lazy-seq (filter
              (fn [x] (->> (take-while (fn [y] (<= (* y y) x)) primes)
                           (map (fn [y] (divides? x y)))
                           (some true?)
                           (not)
                           ))
              (drop 2 (range)))))

(defn get-nth-prime
  {:test #(do
            (assert (= (get-nth-prime 2) 5))
            (assert (= (get-nth-prime 10) 31))
            (assert (= (get-nth-prime 100) 547))
            (assert (= (get-nth-prime 1000) 7927))
            (assert (= (get-nth-prime 10000) 104743))
            )}
  ([n] (nth primes n))
  )

(print (bench (test #'get-nth-prime)))                      ; => "Elapsed time: 165.529 msecs"