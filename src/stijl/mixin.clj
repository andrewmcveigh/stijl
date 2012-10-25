(ns stijl.mixin)

(defn px [n]
  (str n "px"))

(defn rgba [r g b a]
  (format "rgba(%s, %s, %s, %s)" r g b a))

(defn url [x]
  (format "url(%s)" x))

(defmacro defmixin [name & body]
  `(defn ~(with-meta name {:mixin true}) ~@body))

(defmixin border-radius
  ([tl tr bl br]
   (let [corners (mapv px [tl tr bl br])]
     `[:border-radius ~@corners
       :-moz-border-radius ~@corners
       :-webkit-border-radius ~@corners]))
  ([r]
   (border-radius r r r r)))

; vim: lispwords+=defmixin
