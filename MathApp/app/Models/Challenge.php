<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Challenge extends Model
{
    use HasFactory;

    protected $table = 'challenges'; // specify the table name

    protected $primaryKey = 'challengeNo'; // specify the primary key

    protected $fillable = [
        'challengeName',
        'attemptDuration',
        'noOfQuestions',
        'overallMark',
        'openDate',
        'closeDate',
    ];

    // Define relationships if needed, for example:
    public function questionBanks()
    {
        return $this->hasMany(QuestionBank::class, 'challengeNo', 'challengeNo');
    }

    public function attempts()
    {
        return $this->hasMany(Attempt::class, 'challengeNo', 'challengeNo');
    }

    public function participantChallenges()
    {
        return $this->hasMany(ParticipantChallenge::class, 'challengeNo', 'challengeNo');
    }
}

