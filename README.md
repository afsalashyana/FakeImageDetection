#Fake Image Detection Using Machine Learning

The objective of this project is to identify fake images. The problem with existing system is that they are used detect only specific tampering methods(like splicing,coloring etc). We have approached the problem using neural network and machine learning to detect almost all kinds of tampering on images. 

Using Adobe Photoshop, Gimp etc, It is possible to make alterations on image which are too difficult for human eye to detect. Even with a complex neural network, it is not possible to determine whether an image is fake or not without identifying a common factor across almost all fake images. This problem was addressed during feature engineering.

###Feature Engineering
  1. Dr. Neal Krawetz proposed a method called [Error Level Analysis(ELA)] (http://www.hackerfactor.com/papers/bh-usa-07-krawetz-wp.pdf) that exploits the lossy compression of JPEG images. When an image is altered, the compression ratio of the specific portion changes with respect to other parts. A well trained neural network can detect the anomaly by and determine whether the image is fake or not.
  2. The second parameter considered is metadata of the image. A parallel module is added to the program which checks the metadata to determine the signature of various image editing programs. Since it is costly to execute a neural network, the metadata inspection will considerably increase the performance by detecting tampering at an early stage.

###Tools Used

####[Neuroph Studio](http://neuroph.sourceforge.net/)
 Neuroph studio is an open source Java neural network framework that helps to easily build and use neural networks. It also provides a direct interface for loading images
####[Metadata-extractor](https://github.com/drewnoakes/metadata-extractor)
 Metadata-extractor is an open source java library used to extract metadata from images.
####[JavaFX](http://docs.oracle.com/javase/8/javase-clienttechnologies.htm)
 JavaFX is used to implement modern user interface for the application.

###Project Status 
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
- [ ] Reach success rate of 90%

###Screenshots
<img src=http://i.imgur.com/vzfdecs.png>
<img src=http://i.imgur.com/T3TVsuj.png>
<img src=http://i.imgur.com/GLNGz9j.png>
<img src=http://i.imgur.com/z8DzhGD.png>
<img src=http://i.imgur.com/kKfDCOX.png>
