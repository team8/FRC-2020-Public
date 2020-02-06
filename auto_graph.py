import matplotlib.pyplot as plt
import pandas as pd
import numpy as np


def main():
    #To run just put into python terminal.


    #These may need to be changed. I used my absolute paths here, and I am unsure on how to test if this works.

    img = plt.imread('field')
    data = pd.read_csv('auto.csv')
    fig, ax = plt.subplots()

    #These 2 values are the scalars for the size of the field. Shouldn't ever need to touch.
    ax.imshow(img, extent=[0, 15.98, 0, 8.21])

    #Starting position in front of the tower. These can and should change based on the auto. Linewidth just changes the size
    #on the actual graph.
    ax.plot(data.x + 3.4, data.y + 6, linewidth=10)
    plt.show()