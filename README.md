# Fake Image Detection Using Machine Learning

The objective of this project is to identify fake images(Fake images are the images that are digitally altered images). The problem with existing fake image detection system is that they can be used detect only specific tampering methods like splicing, coloring etc. We approached the problem using machine learning and neural network to detect almost all kinds of tampering on images.

Using latest image editing softwares, it is possible to make alterations on image which are too difficult for human eye to detect. Even with a complex neural network, it is not possible to determine whether an image is fake or not without identifying a common factor across almost all fake images. So, instead of giving direct raw pixels to the neural network, we gave error level analysed image.

This project provides two level analysis for the image. At first level, it checks the image metadata. Image metadata is not that much reliable since it can be altered using simple programs. But most of the images we come across will have non-altered metadata which helps to identify the alterations. For example, if an image is edited with Adobe Photoshop, the metadata will contain even the version of the Adobe Photoshop used.

In the second level, the image is converted into error level analysed format and will be resized to 100px x 100px image. Then these 10,000 pixels with RGB values (30,000 inputs) is given in to the input layer of Multilayer perceptron network. Output layer contain two neurons. One for fake image and one for real image. Depending upon the value of these neuron outputs along with metadata analyser output, we determine whether the image is fake or not and how much chance is there for the given image to be tampered.

### Feature Engineering
  1. Dr. Neal Krawetz proposed a method called [Error Level Analysis(ELA)](http://www.hackerfactor.com/papers/bh-usa-07-krawetz-wp.pdf) that exploits the lossy compression of JPEG images. When an image is altered, the compression ratio of the specific portion changes with respect to other parts. A well trained neural network can detect the anomaly by and determine whether the image is fake or not.
  2. The second parameter considered is metadata of the image. A parallel module is added to the program which checks the metadata to determine the signature of various image editing programs. Since it is costly to execute a neural network, the metadata inspection will considerably increase the performance by detecting tampering at an early stage.

### Neural network structure
| Layer | Neurons |
| ------------- | ------------- |
| Input Layer  | 30,000 |
| Hidden Layer 1  | 5000 - Sigmoid |
| Hidden Layer 2  | 1000 - Sigmoid |
| Hidden Layer 3  | 100 - Sigmoid |
| Output Layer  | 2 |


### Watch on YouTube
[![Watch a video](https://img.youtube.com/vi/MVIN9HrS8UY/0.jpg)](https://www.youtube.com/watch?v=MVIN9HrS8UY)

### Tools Used

#### [Neuroph Studio](http://neuroph.sourceforge.net/)
 Neuroph studio is an open source Java neural network framework that helps to easily build and use neural networks. It also provides a direct interface for loading images
#### [Metadata-extractor](https://github.com/drewnoakes/metadata-extractor)
 Metadata-extractor is an open source java library used to extract metadata from images.
#### [JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)
 JavaFX is used to implement modern user interface for the application.

### Flow Chart : Detection
<img src=http://i.imgur.com/TKX7uV6.png>

### Flow Chart : Training
<img src=http://i.imgur.com/wUoo1kb.png>

### Project Status 
- [x] Implement Metadata Procesing Module
- [x] Design a User Interface
- [x] Implement Image Feature Extractor
- [x] Design Neural Network using Neuroph Studio
- [x] Implement Neural Network interface with JavaFX
- [x] Connect Neural Network 
- [x] Integrate Neural Network to Master 
- [x] Train Network
- [x] Test Network
- [x] Read feedback from user and learn instantly
- [x] Add network training interface
- [x] Add module to apply error level analysis on a set of images
- [x] Improve look and feel
- [x] Train with more data
- [x] Add batch testing module
- [x] Detach User Inteface from core
- [x] Implement Command Line Interface
- [ ] Reach success rate of 90%

Journal link : http://www.ijcsits.org/papers/vol7no22017/4vol7no2.pdf

### Screenshots
<img src=https://i.imgur.com/DKMg1ow.gif>
<img src=https://i.imgur.com/vzfdecs.png>
<img src=https://i.imgur.com/T3TVsuj.png>
<img src=https://i.imgur.com/0mzmfFp.png>
<img src=https://i.imgur.com/z8DzhGD.png>
<img src=https://i.imgur.com/mvc9tp0.png>
<img src=https://i.imgur.com/yHQ5JGx.png>
