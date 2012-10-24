(ns stijl.core-test
  (:use clojure.test
        stijl.core))

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
