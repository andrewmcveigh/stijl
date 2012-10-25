(ns stijl.core-test
  (:use clojure.test
        stijl.core
        stijl.mixin))

(deftest css-test
  (testing
    "FIXME, I fail."
    (with-redefs [*whitespace* true]
      (is (= (css
               [$body $#panel $.bootstrap $div.container>li+a
                :padding 0 auto 0 auto
                :margin [:left "auto"
                         :right "auto"]
                [$div#container
                 [$div.well
                  :top "1px"
                  :margin [:bottom "0"
                           :top "10px"]
                  [$a.btn $button.btn $a.btn-primary
                   :border-radius "5px"]]]
                [$span
                 :display "block"]]))
"body, #panel, .bootstrap, div.container > li + a {
padding: 0 auto 0 auto;
margin-left: auto;
margin-right: auto
}

body div#container div.well, #panel div#container div.well, .bootstrap div#container div.well, div.container > li + a div#container div.well {
top: 1px;
margin-bottom: 0;
margin-top: 10px
}
body div#container div.well a.btn, body div#container div.well button.btn, body div#container div.well a.btn-primary, #panel div#container div.well a.btn, #panel div#container div.well button.btn, #panel div#container div.well a.btn-primary, .bootstrap div#container div.well a.btn, .bootstrap div#container div.well button.btn, .bootstrap div#container div.well a.btn-primary, div.container > li + a div#container div.well a.btn, div.container > li + a div#container div.well button.btn, div.container > li + a div#container div.well a.btn-primary {
border-radius: 5px
}
body span, #panel span, .bootstrap span, div.container > li + a span {
display: block
}
"))
    (is (= (css [($input :type "text") "$a[href~=www]" :margin auto])
           "input[type=text], a[href~=www] {\nmargin: auto\n}\n"))
    (is (= (css [$body :margin 0 [$.inlay (border-radius 5) :margin 0]]
                [$div.class (border-radius 10)]))
"body {
margin: 0
}
body .inlay {
border-radius: 5px 5px 5px 5px;
-moz-border-radius: 5px 5px 5px 5px;
-webkit-border-radius: 5px 5px 5px 5px;
margin: 0
}
div.class {
border-radius: 10px 10px 10px 10px;
-moz-border-radius: 10px 10px 10px 10px;
-webkit-border-radius: 10px 10px 10px 10px
}
")))

(deftest in-style-test
  (is (= (in-style :margin [:top 0 :left auto])
         "margin-top: 0;margin-left: auto")))

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
