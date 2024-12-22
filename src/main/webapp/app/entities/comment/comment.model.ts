import dayjs from 'dayjs/esm';
import { IUser } from 'app/entities/user/user.model';
import { ITask } from 'app/entities/task/task.model';

export interface IComment {
  id: number;
  content?: string | null;
  createdAt?: dayjs.Dayjs | null;
  user?: Pick<IUser, 'id'> | null;
  task?: Pick<ITask, 'id'> | null;
}

export type NewComment = Omit<IComment, 'id'> & { id: null };
