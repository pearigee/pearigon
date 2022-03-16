# Pearigon
> Minimal UI. Hotkey Driven. Hackable.

**This project is a work in progress! Expect a bit of turbulence!**

## Features

### Hotkey suggestions

Context sensitive hotkey suggestions are displayed at the top of the screen.

I hope this will make the tool more accessible.

### Search

Pressing `Space` will open the search panel. The results are context
sensitive, so actions that won't do anything do clutter your view.

### Path editing

The path editor is simple but effective. Paths are composed of points that
can be either `:sharp` or `:round`.

New points can be inserted by clicking the pale blue dot between points.

### REPL driven (write code to build shapes)

At any time a Clojure REPL can be opened to write code that manipulates
the project.

The live execution is powered by @borkdude's amazing project [Sci](https://github.com/babashka/sci).

The editor itself is CodeMirror with [Clojure Mode](https://github.com/nextjournal/clojure-mode)
by the lovely folks at NextJournal.

## Local Development

You will need to have the following installed:
* Babashka
* Node
* Clojure CLI

```
npm install
bb dev
```

This will run a live dev (on port 3000) and test (on port 8021) server.

#### Emacs

Once running, use `cider-connect-cljs` to jack in. Happy hacking!
