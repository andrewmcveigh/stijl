(ns stijl.core)

(def ^:dynamic *whitespace* false)

(defn selector? [sym]
  (cond (symbol? sym) (.startsWith (name sym) "$")
        (string? sym) (.startsWith sym "$")
        (list? sym)   (selector? (first sym))))

(defn conv-attrs [[k v]]
  (str \[ (name k) \= v \]))

(defn selector [sym]
  {:pre [(selector? sym)]}
  (cond (symbol? sym) (apply str (rest (name sym)))
        (string? sym) (apply str (rest sym))
        (list? sym)   (apply str
                             (concat (rest (name (first sym)))
                                     (mapcat conv-attrs
                                             (partition 2 (rest sym)))))))

(defn sub-selector? [form]
  (when (vector? form) (selector? (first form))))

(defn mixin? [form]
  (when (list? form) (-> form first resolve meta :mixin)))

(defn rules? [form]
  (not (if (vector? form)
         (sub-selector? form)
         (or (selector? form) (mixin? form)))))

(defn collapse-properties [[[k] vs]]
  (if (vector? (first vs))
    (mapv (fn [[k2 v]]
            (str (name k) \- (name k2) ": " v))
          (partition 2 (first vs)))
    [(apply str
            (str (name k) ": ")
            (interpose \space (map #(if (symbol? %) (name %) %) vs)))]))

(defn pair-properties [rules]
  (partition 2 (partition-by keyword? rules)))

(defn css-endline [line-seq]
  (apply str (interpose (str \; (when *whitespace* \newline)) line-seq)))

(defn render-selector [selector]
  (let [selector (re-seq #"([^>\+\*]+)([>\+\*]?)" (name selector))]
    (apply str (interpose \space
                          (filter seq (reduce concat (map rest selector)))))))

(defn render-css
  ([parents forms]
   (apply str
          (for [{:keys [selectors properties children]} forms]
            (let [selectors (map render-selector selectors)
                  selectors (if (seq parents)
                              (for [p parents s selectors]
                                (str p \space s))
                              selectors)]
              (str (when (seq properties)
                     (str (apply str (interpose ", " selectors)) \space
                          \{ \newline properties \newline \}))
                   \newline
                   (when children (render-css selectors children)))))))
  ([forms]
   (render-css nil forms)))

(defn render-properties [form]
  (css-endline
    (mapcat collapse-properties
            (pair-properties (filter rules? form)))))

(defmacro css-map [& body]
  (mapv (fn [form]
          (let [mixins (filter mixin? form)
                form   (remove mixin? form)]
            {:selectors  (mapv selector (filter selector? form))
             :properties `(render-properties (concat ~@mixins '~form))
             :children   (when-not (nil? (seq (filter sub-selector? form)))
                           (cons 'css-map (filter sub-selector? form)))}))
        body))

(defmacro css [& body]
  `(binding [*whitespace* true]
     (render-css (css-map ~@body))))

(defmacro style [& body]
  `[:style {:type "text/css"} (css ~@body)])

(defmacro in-style [& body]
  (binding [*whitespace* false]
    (render-properties body)))

(defn px [n]
  (str n "px"))

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
