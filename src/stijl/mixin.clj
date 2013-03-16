; vim: lispwords+=defmixin

(ns stijl.mixin
  (:require [clojure.string :as string]))

(defn px [n]
  (str n "px"))

(defn rgb [r g b]
  (format "rgb(%s, %s, %s)" r g b))

(defn rgba [r g b a]
  (format "rgba(%s, %s, %s, %s)" r g b a))

(defn color-stop
  ([color stop]
   (str color " " stop))
  ([color]
   (color-stop color nil)))

(defn url [x]
  (format "url(%s)" x))

(defmacro defmixin [name & body]
  `(defn ~(with-meta name {:stijl/mixin true}) ~@body))

(defmixin border-radius
  ([tl tr bl br]
   (let [corners (mapv px [tl tr bl br])]
     `[:border-radius ~@corners
       :-moz-border-radius ~@corners
       :-webkit-border-radius ~@corners]))
  ([r]
   (border-radius r r r r)))

(defmixin box-shadow [& specs]
  `[:box-shadow ~@specs
    :-moz-box-shadow ~@specs
    :-webkit-box-shadow ~@specs])

(defmixin css-radial-gradient [spec & color-stops]
  [:background-image (str "-o-radial-gradient("
                          (string/join ", " (cons spec color-stops)) ")")
   :background-image (str "-moz-radial-gradient("
                          (string/join ", " (cons spec color-stops)) ")")
   :background-image (str "-webkit-radial-gradient("
                          (string/join ", " (cons spec color-stops)) ")")
   :background-image (str "-ms-radial-gradient("
                          (string/join ", " (cons spec color-stops)) ")")
   :background-image (str "radial-gradient("
                          (string/join ", " (cons spec color-stops)) ")")])
