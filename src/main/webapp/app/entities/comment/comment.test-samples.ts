import dayjs from 'dayjs/esm';

import { IComment, NewComment } from './comment.model';

export const sampleWithRequiredData: IComment = {
  id: 3923,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-18T23:59'),
};

export const sampleWithPartialData: IComment = {
  id: 9125,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-18T23:51'),
};

export const sampleWithFullData: IComment = {
  id: 26805,
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-18T22:27'),
};

export const sampleWithNewData: NewComment = {
  content: '../fake-data/blob/hipster.txt',
  createdAt: dayjs('2024-12-18T13:29'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
