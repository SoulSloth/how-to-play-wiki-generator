# How to play wiki Site Generator

Static site generator for a simple and fast gaming wiki.

## Usage

### Development

For development you can launch the ring server by running the following in the project directory:

```shell
lein ring server
```

This will launch a server which will serve the site on localhost from the `resources` directory. Changes you make to the code and pages will be reflected when you refresh the site.

The ring development server will look for pages in the `site-content` directory while Optimis will check for assets in the `optimusAssets` directory. Creating symlinks for these files to the content-repo on my local drive is how I'm currently doing development.

### Artifact

The site can be exported one of two ways:

```shell
#Using Lein task
lein build-site "input-dir" "output-dir"

#As a standalone uberjar
lein uberjar

java -jar ./target/uberjar/{your standalone artifact}.jar "input-dir" "output-dir"
```
