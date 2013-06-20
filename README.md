Dotify
======

It's all in the name. Dotify pictures of your friends, your enemies, and especially your cat.

Dotify is a pretty neat image manipulation tool that I built entirely with Java and Swing.
In a nutshell it uses a variation of Dijkstra's algorithm and leverages both the darkness and the
gradient difference in darkness between adjacent pixels to calculate path lengths. It compares the path 
length with a threshold and when this threshold is surpassed, it places a stipple point in the image.  Each 
time a stipple is placed, the interior path length is set to zero and the process repeats until every pixel
in the image has been visited.

Below are a few of my favorite dotified images.

![Barcelona Lion](https://github.com/mlp5ab/Dotify/tree/master/examples/barcalion.jpg)

![Suits](https://github.com/mlp5ab/Dotify/tree/master/examples/class.png)

![Colloseum](https://github.com/mlp5ab/Dotify/tree/master/examples/colo.png)

![Guitar](https://github.com/mlp5ab/Dotify/tree/master/examples/guitar.tiff)

![Tea](https://github.com/mlp5ab/Dotify/tree/master/examples/jason.tiff)

![Glasses](https://github.com/mlp5ab/Dotify/tree/master/examples/lucas_looney.png)

![Spanish Chica](https://github.com/mlp5ab/Dotify/tree/master/examples/maria.tiff)

![Token Kitty](https://github.com/mlp5ab/Dotify/tree/master/examples/mj.tiff)

![Alma Mater](https://github.com/mlp5ab/Dotify/tree/master/examples/rotunda.jpg)
