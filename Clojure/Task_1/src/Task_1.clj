(ns Task_1)
(require '[clojure.string :as str])

(defn possibleSymbols [word alphabet]
  (filter #(not (str/ends-with? word %)) alphabet))

(defn makeWords [word alphabet]
  (map #(str word %) (possibleSymbols word alphabet)))

(defn makeList [wordList alphabet]
  (reduce concat (map #(makeWords % alphabet) wordList)))

(defn main [N alphabet]
  (reduce (fn [l, _] (makeList l alphabet)) (list "") (range N)))
(print (main 2 '("a" "b" "c"))) ; => (ab ac ba bc ca cb)