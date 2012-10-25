(ns stijl.core)

(def css-units ["em" "ex" "px" "ch" "in" "cm" "mm" "pt" "pc"])

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
  (when (list? form) (-> form first resolve meta :mixin)))

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

(defn quote-prop-syms [form]
  (cond (symbol? form) (let [s (name form)
                             n (re-seq css-re-pattern s)]
                         (if n (apply str (reverse (nfirst n))) s))
        (vector? form) (mapv quote-prop-syms form)
        :default    form))

(defmacro css-map [& body]
  (mapv (fn [form]
          (let [mixins (filter mixin? form)
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

(use 'stijl.mixin)

(apply str (reverse (nfirst (re-seq #"(px|em)(-?\d+)" "px-80"))))

(css [$demo :margin-top px80])
(css [$demo :margin-top px-8])

(css-map [$ttt (border-radius 4)
          :ttt (rgba 1 2 3 5)])
(style
    [$body :background-color (rgba 42 153 255 0.17)]
    [$.well
     :background-color (rgba 0 0 0 0.05)
     :overflow hidden
     :padding [:bottom 0]])

(style
  [$body :background-color (rgba 42 153 255 0.17)]
  [$.well
   :background-color (rgba 0 0 0 0.05)
   :overflow hidden
   :padding [:bottom 0]]
  [$input $textarea $select $.uneditable-input
   :border (px 1) solid (rgba 0 0 0 0.25)
   :background-color (rgba 255 255 255 0.9)]
  [$.input-prepend $.input-append
   [$.add-on :border (px 1) solid (rgba 0 0 0 0.25)]]
  [$.btn :border [:color (rgba 0 0 0 0.5)]]
  [$.form-horizontal
   [$.form-actions :padding [:left (px 180)]]]
  [$.form-actions
   :background-color (rgba 0 0 0 0.05)
   :border [:top solid (px 1) (rgba 0 0 0 0.2)]
   :margin "18px -20px -20px"]
  [$legend :border [:bottom solid (px 1) (rgba 0 0 0 0.2)]]
  [$.navbar-inner
   :background [:color "#9AB"
                :image (url "/img/linen-grad.png")
                :repeat repeat-x]
   :box-shadow "0 1px 3px rgba(0, 0, 0, 0.50), 0 -1px 0 rgba(0, 0, 0, 0.3) inset"
   :min-height (px 40)]
  [$.navbar
   [$.brand
    :font [:family Arial]
    :text-shadow 0 (px -1) 0 (rgba 0 0 0 0.77) \, 0 (px 1) 0 (rgba 255 255 255 0.2)]])
