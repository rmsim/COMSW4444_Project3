import matplotlib.pyplot as plt
import os
import re

if __name__ == "__main__":
    data = []
    dataI = {}
    for filename in os.listdir("."):
        if "stats" in filename:
            i = int(re.search(r'\d+', filename).group())
            data = []
            with open(filename) as fin:
                data += fin.read().split()
                fin.close()
            data = [int(d) for d in data]
            if i in dataI.keys():
                dataI[i] += data
            else:
                dataI[i] = data

    for i in dataI.keys():
        plt.hist(dataI[i], bins = 50, ec = 'black')
        result = sorted(dataI[i])
        l60 = result[int(60*len(result)/100)]
        print(i, l60)
        plt.title("Socks: " + str(i) + ", 60:40 point:" + str(l60))
        plt.axvline(l60, color='r')
        plt.xlabel("Sock distance")
        plt.ylabel("Frequency")
        plt.show()