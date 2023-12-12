(ns Task_1)
(require '[clojure.string :as str])

; вернуть символы из алфавита, кроме того, который является последним в слове
(defn possible-symbols [word alphabet]
  (filter #(not (str/ends-with? word %)) alphabet))

; возвращает слова составленные из допустимых символов и переданного слова
(defn make-words [word alphabet]
  (map #(str word %) (possible-symbols word alphabet)))

; возвращает список новых слов составленных из слов в wordList
; (для каждого слова отсюда составляет новые слова)
(defn make-list [wordList alphabet]
  (reduce concat (map #(make-words % alphabet) wordList)))

; N раз вызывает функцию makeList для первоначального списка, состоящего из пустого символа
(defn main [N alphabet]
  (reduce (fn [l, _] (make-list l alphabet)) (list "") (range N)))
(print (main 3 '("a" "b" "c")))
; => (aba abc aca acb bab bac bca bcb cab cac cba cbc)