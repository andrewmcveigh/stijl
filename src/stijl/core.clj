(ns stijl.core)

(def css-units ["em" "ex" "px" "ch" "in" "cm" "mm" "pt" "pc" "%"])

(def css-re-pattern
  (re-pattern (str (apply str \( (interpose \| css-units)) ")(-?\\d+)")))

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
  (when (list? form) (-> form first resolve meta :stijl/mixin)))

(defn rules? [form]
  (not (if (vector? form)
         (sub-selector? form)
         (or (selector? form) (mixin? form)))))

(defn collapse-properties [[[k] vs]]
  (cond (vector? (first vs)) (mapv (fn [[k2 v]]
                                     (str (name k) \- (name k2) ": " v))
                                   (partition 2 (first vs)))
        :default [(apply str
                         (str (name k) ": ")
                         (interpose \space (map #(if (symbol? %) (name %) %) vs)))]))

(defn pair-properties [rules]
  (partition 2 (partition-by keyword? rules)))

(defn css-endline [line-seq]
  (apply str (concat (interpose (str \; (when *whitespace* \newline)) line-seq)
                     (when (seq line-seq) [\;]))))

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

(defn render-properties [rules]
  (css-endline
    (mapcat collapse-properties
            (pair-properties rules))))

(defn quote-prop-syms
  ([form quote-normal-syms?]
   (cond (symbol? form) (let [s (name form)
                              n (re-seq css-re-pattern s)]
                          (if n
                            (apply str (reverse (nfirst n)))
                            (if quote-normal-syms? s form)))
         (vector? form) (mapv quote-prop-syms form)
         (list? form)   (cons (first form) (map quote-prop-syms (rest form)))
         :default    form))
  ([form]
   (quote-prop-syms form true)))

(defmacro css-map [& body]
  (mapv (fn [form]
          (let [mixins (map #(quote-prop-syms % false) (filter mixin? form))
                rules  (filter rules? form)
                rules  (mapv quote-prop-syms rules)]
            {:selectors  (mapv selector (filter selector? form))
             :properties `(render-properties (concat ~@mixins ~rules))
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
