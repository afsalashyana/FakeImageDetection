# FakeImageDetection
Fake Image Detection Using Machine Learning

This branch works on the implementation of Neural Network.

Currently a simple 2 layer NN is used. 

TRAINING DATA

Training data is taken from src/main/resources/TrainingData.txt. The image to be trained is first applied with Laplace filter.It is then converted in to an array of pixels (only brightness is taken since the image is grayscale). Image should be of 128x128px size. This leades to 16384 pixels and 16385th pixel is used for indicating whether the image is real or fake (0 and 1 is used).

As of now, Network will take 16385 inputs.
