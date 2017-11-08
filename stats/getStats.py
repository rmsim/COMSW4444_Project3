import random as r
import math
import statistics
import time
import maxWeight as b

def getSock():
    return (r.randint(0, 256), r.randint(0, 256), r.randint(0, 256))

def getDistance(s1, s2):
    return int(math.sqrt((s1[0]-s2[0])**2 + (s1[1]-s2[1])**2 + (s1[2]-s2[2])**2))

def makeAndGetDistance():
    return getDistance(getSock(), getSock())

if __name__ == "__main__":
    for i in [10, 20, 50, 100, 200]:
        distance = []
        socks = []
        for j in range(2*i):
            socks.append(getSock())

        matrix = []
        for k in range(2*i):
            for j in range(k+1, 2*i):
                matrix.append((k, j, -getDistance(socks[k], socks[j])))

        match = b.maxWeightMatching(matrix, True)
        result = []

        for k in range(len(match)):
            if (match[k] < k):
                continue
            result.append(socks[k])
            result.append(socks[match[k]])

        with open("stats_" + str(2*i) + "_" + str(time.time()) + ".txt", "w") as fin:
            for k in range(0, 2*i, 2):
                fin.write(str(getDistance(result[k], result[k+1])) + "\n")
            fin.close()
        


            