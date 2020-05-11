# sudoku-cljs

A [re-frame](https://github.com/Day8/re-frame) application that acts as an interactive sudoku solver. Try it [here.](https://ethan-sorrell.github.io/sudoku-cljs/)

## Progress:
After ~6 months I'm finally getting around to cleaning this up. Refactored the code structure to a point that I pretty happy with it. If I can be bothered to refresh on clojure I'll probably rewrite a lot of the functions which appear overly verbose/cryptic (unnecessary use of if-not and similar). Then I hope to clean up the html/css enough to make the page somewhat presentable and hopefully add a gif/video presentation here.

Also thanks to [strager](https://www.twitch.tv/strager) for providing the very helpful code review despite not being familiar with clojure.

## Run application:

```
lein clean
lein dev
```

shadow-cljs will automatically push cljs changes to the browser.

Wait a bit, then browse to [http://localhost:8280](http://localhost:8280).
