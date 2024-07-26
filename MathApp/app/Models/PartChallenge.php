<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class PartChallenge extends Model
{
    use HasFactory;

    // Define the table associated with the model
    protected $table = 'part_challenge';

    // Primary keys
    protected $primaryKey = ['challengeNo', 'participantID'];
    public $incrementing = false;
    protected $keyType = 'string';

    // Define the fillable attributes
    protected $fillable = [
        'challengeNo',
        'participantID',
    ];

    // Define the relationships
    public function challenge()
    {
        return $this->belongsTo(Challenge::class, 'challengeNo', 'challengeNo');
    }

    public function participant()
    {
        return $this->belongsTo(Participant::class, 'participantID', 'participantID');
    }
}
