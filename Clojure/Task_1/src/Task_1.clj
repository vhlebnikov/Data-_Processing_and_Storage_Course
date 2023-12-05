(ns Task_1)
(require '[clojure.string :as str])

; вернуть символы из алфавита, кроме того, который является последним в слове
(defn possibleSymbols [word alphabet]
  (filter #(not (str/ends-with? word %)) alphabet))

; возвращает слова составленные из допустимых символов и переданного слова
(defn makeWords [word alphabet]
  (map #(str word %) (possibleSymbols word alphabet)))

; возвращает список новых слов составленных из слов в wordList
; (для каждого слова отсюда составляет новые слова)
(defn makeList [wordList alphabet]
  (reduce concat (map #(makeWords % alphabet) wordList)))

; N раз вызывает функцию makeList для первоначального списка, состоящего из пустого символа
(defn main [N alphabet]
  (reduce (fn [l, _] (makeList l alphabet)) (list "") (range N)))
(print (main 2 '("a" "b" "c"))) ; => (ab ac ba bc ca cb)