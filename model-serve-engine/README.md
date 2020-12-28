#Hints for module flowers-model-serve:

This module serves the ML model trained in flowers-model-training.

So, what does 'serve' mean in this context? 

Firstly, it provides an API to have images (or generally documents) classified.
The return value is not a prediction or label, but a more detailed classification summary which can be use for further 
processing in a document workflow. 

Therefore, this module doesnt just predict, but also calculates a confidence level and randomly assigns documents 
for human classification. These steps are tracked. 




# OCR functionality and specific requirements for Spark Cluster Machines
This project uses Tesseract / Tess4J for Ocr functionality. 
As of November 2020, tesseract uses native C/C++ Code to do the heavy lifting, Tess4J is just a small JVM wrapper around it.
Therefore, all Spark Cluster Machines and the developer's local  workstation need an installation of native tesseract.
On most Linux Distros there's a software package available via apt-get / yum / dnf.

I recommend configuring the remote Spark Cluster Machines using Ansible.     

  
