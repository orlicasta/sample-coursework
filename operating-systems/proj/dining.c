#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <time.h>
#include <pthread.h>
#include <signal.h>
#include <semaphore.h>

#define PHILOSOPHER_NUM 5
#define THINKING 0
#define HUNGRY 1
#define EATING 2

int MAX_MEALS = 10;
float MAX_THINK_EAT_SEC = 4;
int state[PHILOSOPHER_NUM];
int philosopherList[PHILOSOPHER_NUM];
int meals[PHILOSOPHER_NUM];
int isRunning = 1;

sem_t mutex;
sem_t chop[PHILOSOPHER_NUM];
pthread_t tid[PHILOSOPHER_NUM];

void test(int phil) {

	//if philosopher is HUNGRY and neighbors are not EATING
	if (state[phil] == HUNGRY 
	&& state[(phil - 1) % PHILOSOPHER_NUM] != EATING
	&& state[(phil + 1) % PHILOSOPHER_NUM] != EATING) {	
		
		//update state
		state[phil] = EATING;
		fprintf(stderr, "philosopher %d is EATING with chopsticks %d & %d\n", 
				phil, phil, (phil + 1) % PHILOSOPHER_NUM);

		//count meal
		meals[phil]++;
		fprintf(stderr, "philosopher %d begins meal %d\n", phil, meals[phil]);
	
		//post chopstick	
		sem_post(&chop[phil]);
	}

	return;
}

void pickup_fork(int phil) {

	//wait for mutex
	sem_wait(&mutex);

	//update state
	state[phil] = HUNGRY;
	fprintf(stderr, "philosopher %d is HUNGRY\n", phil);

	//eat if possible
	test(phil);
	
	//post mutex
	sem_post(&mutex);
	
	//wait for chopstick
	if (state[phil] != EATING) {
		sem_wait(&chop[phil]);
	}
	
	return;
}

void return_fork(int phil) {
	
	//wait for mutex
	sem_wait(&mutex);

	//update state
	state[phil] = THINKING;
	fprintf(stderr, "philosopher %d finishes meal %d and puts down chopsticks %d & %d and is THINKING\n", 
			phil, meals[phil], phil, (phil + 1) % PHILOSOPHER_NUM);

	//test neighbors
	test((phil - 1) % PHILOSOPHER_NUM);
	test((phil + 1) % PHILOSOPHER_NUM);

	//post mutex	
	sem_post(&mutex);

	return;
}

void* philosopher(void* phil) {

	//philosopher num
	int* i = phil;
	
	while (isRunning) {
		
		pickup_fork(*i);
		//eat for 1 to MAX_THINK_EAT_SEC
		sleep(1.0 + (float)rand() / ((float)RAND_MAX / (MAX_THINK_EAT_SEC - 1.0)));
		
		return_fork(*i);
		//think for 1 to MAX_THINK_EAT_SEC
		sleep(1.0 + (float)rand() / ((float)RAND_MAX / (MAX_THINK_EAT_SEC - 1.0)));
		
		//end thread once MAX_MEALS have been eaten
		if (meals[*i] > MAX_MEALS - 1) {
			sem_post(&chop[*i]);
			pthread_kill(tid[*i], 0);
			sem_post(&mutex);
			fprintf(stderr, "philosopher %d is DEAD\n", *i);
			
			return NULL;
		}
	}

	return NULL;
}

int main (int argc, char *argv[]) {

	//seed random num
	time_t t;
	srand((unsigned) time(&t));
	
	//initialize mutex
	sem_init(&mutex, 0, 1);

	//initialize chopstick semaphores and philosophers
	for (int i = 0; i < PHILOSOPHER_NUM; i++) {
		//initialize semaphore	
		sem_init(&chop[i], 0, 0);
		//number philospher
		philosopherList[i] = i;
		//initialize philosopher state
		state[i] = THINKING;
		//initialize meal count
		meals[i] = 0;
	}

	//create threads 	
	for (int i = 0; i < PHILOSOPHER_NUM; i++) {
		pthread_create(&tid[i], NULL, philosopher, &philosopherList[i]);
		fprintf(stderr, "philosopher %d is THINKING\n", i);
	}

	//run for user specified time
	if (argc > 1) {
		sleep(atof(argv[1]));
	}
	else {
		fprintf(stderr, "----> run_time argument unspecified ----> running for 10 sec\n");
		sleep(10);
	}
	isRunning = 0;

	//kill remaining threads
	for (int i = 0; i < PHILOSOPHER_NUM; i++) {
		pthread_kill(tid[i], 0);
	}

	fprintf(stderr, "\n");

	//display results
	if (argc > 1) {
		printf("%.2f seconds are up - chopsticks down!\n", atof(argv[1]));
	}
	else {
		printf("10.00 seconds are up - chopsticks down!\n");

	}

	int totalMeals = 0;
	int minMeals = MAX_MEALS;
	int maxMeals = 0;

	for (int i = 0; i < PHILOSOPHER_NUM; i++) {
		printf("philosopher %d ate %d meals\n", i, meals[i]);
		totalMeals += meals[i];
		if (meals[i] < minMeals) {
			minMeals = meals[i];
		}
		if (meals[i] > maxMeals) {
			maxMeals = meals[i];
		}
	}

	printf("minimum meals:\t%d\n", minMeals);
	printf("maximum meals:\t%d\n", maxMeals);
	printf("average meals:\t%.2f\n\n", (float)totalMeals / (float)PHILOSOPHER_NUM);

	return 0;
}
